package com.helge.moviebook.repository.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.helge.moviebook.vo.RecentQuery
import java.util.*

@Entity(tableName = "recent_queries")
data class RecentEntity(
    @field:PrimaryKey
    val query: String, // primary `cause we don't need duplicate queries
    val createdOn: Calendar = Calendar.getInstance()
) {
    constructor(model: RecentQuery) : this(
        query = model.query,
        createdOn = model.createdOn
    )

    fun toModel(): RecentQuery {
        return RecentQuery(
            query = query,
            createdOn = createdOn
        )
    }

    @Dao
    interface Store {
        @Query("SELECT * FROM recent_queries ORDER BY `createdOn` DESC")
        fun all(): LiveData<List<RecentEntity>>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun save(entity: RecentEntity)

        @Query("DELETE FROM recent_queries where createdOn NOT IN " +
                "(SELECT createdOn from recent_queries ORDER BY createdOn DESC LIMIT $MAX_ROWS)")
        suspend fun limitRows()

        @Delete
        suspend fun delete(entity: RecentEntity)

        companion object {
            const val MAX_ROWS = 20
        }
    }

}
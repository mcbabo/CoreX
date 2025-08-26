package at.mcbabo.corex.data.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface BackupDao {
    @RawQuery
    fun checkpoint(query: SupportSQLiteQuery): Int
}
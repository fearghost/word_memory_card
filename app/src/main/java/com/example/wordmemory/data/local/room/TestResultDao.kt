package com.example.wordmemory.data.local.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TestResultDao {
    @Insert
    suspend fun insert(result: TestResultEntity)

    @Query("SELECT * FROM test_results WHERE bundleId = :bundleId ORDER BY date DESC LIMIT :limit")
    fun getRecentTestResults(bundleId: Long, limit: Int = 5): Flow<List<TestResultEntity>>

    @Query("DELETE FROM test_results WHERE id NOT IN (SELECT id FROM test_results WHERE bundleId = :bundleId ORDER BY date DESC LIMIT 5) AND bundleId = :bundleId")
    suspend fun pruneOldResults(bundleId: Long)

    // Alias for WordRepository compatibility
    suspend fun insertTestResult(result: TestResultEntity) {
        insert(result)
    }
}

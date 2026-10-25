package com.example.wordmemory.data.local.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM word_bundles ORDER BY createdAt DESC")
    fun getAllBundles(): Flow<List<WordBundleEntity>>

    @Query("""
        SELECT wb.*, COUNT(wi.id) as wordCount 
        FROM word_bundles wb 
        LEFT JOIN word_items wi ON wb.id = wi.bundleId 
        GROUP BY wb.id 
        ORDER BY wb.createdAt DESC
    """)
    fun getAllBundlesWithWordCount(): Flow<List<BundledWordCount>>

    data class BundledWordCount(
        val id: Long,
        val name: String,
        val createdAt: Long,
        val wordCount: Int
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBundle(bundle: WordBundleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<WordItemEntity>)

    @Query("SELECT * FROM word_items WHERE bundleId = :bundleId")
    suspend fun getItemsByBundle(bundleId: Long): List<WordItemEntity>

    @Query("SELECT * FROM word_items WHERE bundleId = :bundleId")
    fun getWordsByBundleFlow(bundleId: Long): Flow<List<WordItemEntity>>

    @Query("SELECT * FROM word_items WHERE bundleId = :bundleId AND isDifficult = 1")
    suspend fun getDifficultItemsByBundle(bundleId: Long): List<WordItemEntity>

    @Query("UPDATE word_items SET isDifficult = :isDifficult WHERE id = :itemId AND bundleId = :bundleId")
    suspend fun toggleDifficult(itemId: Long, bundleId: Long, isDifficult: Boolean)

    @Query("DELETE FROM word_items WHERE bundleId = :bundleId")
    suspend fun deleteItemsByBundleId(bundleId: Long)

    @Delete
    suspend fun deleteBundle(bundle: WordBundleEntity)

    @Query("SELECT COUNT(*) FROM word_items WHERE bundleId = :bundleId")
    suspend fun getWordCountByBundleId(bundleId: Long): Int
}

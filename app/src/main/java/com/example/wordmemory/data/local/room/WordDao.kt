package com.example.wordmemory.data.local.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM word_bundles ORDER BY createdAt DESC")
    fun getAllBundles(): Flow<List<WordBundleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBundle(bundle: WordBundleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<WordItemEntity>)

    @Query("SELECT * FROM word_items WHERE bundleId = :bundleId")
    suspend fun getItemsByBundle(bundleId: Long): List<WordItemEntity>

    @Query("DELETE FROM word_items WHERE bundleId = :bundleId")
    suspend fun deleteItemsByBundleId(bundleId: Long)

    @Delete
    suspend fun deleteBundle(bundle: WordBundleEntity)
}

package com.example.labelguard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.labelguard.data.model.ProductScanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductScanDao {

    @Query("SELECT * FROM product_scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ProductScanEntity>>

    @Query("SELECT * FROM product_scans WHERE id = :id")
    fun getScanById(id: Long): Flow<ProductScanEntity?>

    @Query("SELECT * FROM product_scans WHERE complianceStatus = :status ORDER BY timestamp DESC")
    fun getScansByStatus(status: String): Flow<List<ProductScanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ProductScanEntity): Long

    @Query("DELETE FROM product_scans WHERE id = :id")
    suspend fun deleteScanById(id: Long): Int

    @Query("DELETE FROM product_scans")
    suspend fun clearAllScans()

    @Query("SELECT COUNT(*) FROM product_scans")
    fun getScansCount(): Flow<Int>
}

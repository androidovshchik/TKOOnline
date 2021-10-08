package ru.iqsolution.tkoonline.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.iqsolution.tkoonline.local.entities.Draft

@Dao
abstract class DraftDao {

    @Query(
        """
        SELECT drafts.* FROM drafts 
        INNER JOIN tokens ON d_token_id = t_id
        WHERE t_car_id = :car AND d_route_id == :route AND d_day LIKE :day || '%'
    """
    )
    abstract suspend fun getByRouteDay(car: Int, route: String?, day: String): List<Draft>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(item: Draft): Long

    @Query(
        """
        UPDATE drafts 
        SET d_status = :status
        WHERE d_id == :id
    """
    )
    abstract suspend fun updateStatus(id: Long?, status: Int)
}
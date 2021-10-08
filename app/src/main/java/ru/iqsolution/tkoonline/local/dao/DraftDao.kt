package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
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
    abstract fun insertAll(items: List<Draft>)

    @Transaction
    open suspend fun safeInsert(car: Int, route: String?, day: String, items: List<Draft>) {
        deleteAll(getByRouteDay(car, route, day).map { it.id })
        insertAll(items)
    }

    @Query(
        """
        UPDATE drafts 
        SET d_status = :status
        WHERE d_id == :id
    """
    )
    abstract suspend fun updateStatus(id: Long?, status: Int)

    @Query(
        """
        DELETE FROM drafts
        WHERE d_id in (:ids)
    """
    )
    abstract fun deleteAll(ids: List<Long?>)
}
package ru.iqsolution.tkoonline.local.dao

import androidx.room.*
import ru.iqsolution.tkoonline.local.entities.Route

@Dao
@Suppress("FunctionName")
abstract class RouteDao {

    @Query(
        """
        SELECT routes.* FROM routes 
        INNER JOIN tokens ON r_token_id = t_id
        WHERE t_car_id = :car AND r_when_day LIKE :day || '%'
    """
    )
    abstract suspend fun getByDay(car: Int, day: String): List<Route>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _insertAll(items: List<Route>)

    @Transaction
    open suspend fun replaceAll(car: Int, day: String, items: List<Route>) {
        _deleteAll(getByDay(car, day).map { it.id })
        _insertAll(items)
    }

    @Query(
        """
        DELETE FROM routes
        WHERE r_id in (:ids)
    """
    )
    abstract fun _deleteAll(ids: List<Long?>)
}
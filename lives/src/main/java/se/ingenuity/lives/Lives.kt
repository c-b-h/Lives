package se.ingenuity.lives

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

object Lives {
    fun <T1, T2, R> combineLatest(
        first: LiveData<out T1>,
        second: LiveData<out T2>,
        combineFunction: (T1, T2) -> R
    ): LiveData<R> {
        @Suppress("UNCHECKED_CAST")
        return combineLatest(
            Array2Func(combineFunction),
            first as LiveData<Any>,
            second as LiveData<Any>
        )
    }

    fun <T1, T2, T3, R> combineLatest(
        first: LiveData<out T1>,
        second: LiveData<out T2>,
        third: LiveData<out T3>,
        combineFunction: (T1, T2, T3) -> R
    ): LiveData<R> {
        @Suppress("UNCHECKED_CAST")
        return combineLatest(
            Array3Func(combineFunction),
            first as LiveData<Any>,
            second as LiveData<Any>,
            third as LiveData<Any>
        )
    }

    fun <T1, T2, T3, T4, R> combineLatest(
        first: LiveData<out T1>,
        second: LiveData<out T2>,
        third: LiveData<out T3>,
        fourth: LiveData<out T4>,
        combineFunction: (T1, T2, T3, T4) -> R
    ): LiveData<R> {
        @Suppress("UNCHECKED_CAST")
        return combineLatest(
            Array4Func(combineFunction),
            first as LiveData<Any>,
            second as LiveData<Any>,
            third as LiveData<Any>,
            fourth as LiveData<Any>
        )
    }

    private fun <T, R> combineLatest(
        combiner: (Array<in T>) -> R,
        vararg sources: LiveData<T>
    ): LiveData<R> {
        val result: MediatorLiveData<R> = MediatorLiveData()

        val values = arrayOfNulls<Any>(sources.size)
        val emits = BooleanArray(sources.size)
        sources.forEachIndexed { index, liveData ->
            result.addSource(liveData) { value ->
                emits[index] = true
                values[index] = value

                if (emits.all { it }) {
                    result.postValue(combiner(values))
                }
            }
        }

        return result
    }

    private class Array2Func<T1, T2, R>(
        private val f: (T1, T2) -> R
    ) : (Array<*>) -> R {
        @Suppress("UNCHECKED_CAST")
        override fun invoke(a: Array<*>): R {
            require(a.size == 2) { "Array of size 2 expected but got " + a.size }
            return f(a[0] as T1, a[1] as T2)
        }
    }

    private class Array3Func<T1, T2, T3, R>(
        private val f: (T1, T2, T3) -> R
    ) : (Array<*>) -> R {
        @Suppress("UNCHECKED_CAST")
        override fun invoke(a: Array<*>): R {
            require(a.size == 3) { "Array of size 3 expected but got " + a.size }
            return f(a[0] as T1, a[1] as T2, a[2] as T3)
        }
    }

    private class Array4Func<T1, T2, T3, T4, R>(
        private val f: (T1, T2, T3, T4) -> R
    ) : (Array<*>) -> R {
        @Suppress("UNCHECKED_CAST")
        override fun invoke(a: Array<*>): R {
            require(a.size == 4) { "Array of size 4 expected but got " + a.size }
            return f(a[0] as T1, a[1] as T2, a[2] as T3, a[3] as T4)
        }
    }
}
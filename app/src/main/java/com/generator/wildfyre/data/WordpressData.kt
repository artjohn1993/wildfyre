package com.generator.wildfyre.data

import com.generator.wildfyre.enum.DownloadStatus
import com.generator.wildfyre.model.Wordpress
import kotlinx.coroutines.CompletionHandler

class WordpressData {

    fun factorWordpress(
        data: List<Wordpress.Result>,
        factor: Int
    ): MutableList<Wordpress.Result> {
        var total = data.count() / factor
        if (data.count() < factor) {
            total = 1
        }
        var newFactoredWordpress: MutableList<Wordpress.Result> = ArrayList()
        data.shuffled().forEach { item ->
            if (newFactoredWordpress.count() < total) {
                newFactoredWordpress.add(item)
            }
        }

        var withEmptyLink: MutableList<Wordpress.Result> = ArrayList()
        newFactoredWordpress.forEach { item ->
            withEmptyLink.add(item)
            var emptyData = Wordpress.Result("","about:blank","",Wordpress.Title(""))
            withEmptyLink.add(emptyData)
        }
        println("total with Empty link ${withEmptyLink.count()}")
        return withEmptyLink
    }

    fun getTotalFactoredWordpress(total: Int, factor: Int): Int {
        if (factor > total) {
            return 1
        } else {
            return total / factor
        }
    }

    fun prepareDownloadedData(
        data: List<Wordpress.Result>,
        page: Int,
        completionHandler: (DownloadStatus) -> Unit
    ) {

        if (!data.isEmpty()) {
            completionHandler.invoke(DownloadStatus.NEXT)
        } else if (data.isEmpty() && page > 1) {
            completionHandler.invoke(DownloadStatus.DONE)
        } else {
            completionHandler.invoke(DownloadStatus.EMPTY)
        }
    }

    fun addWordpressData(
        array: MutableList<Wordpress.Result>,
        data: List<Wordpress.Result>,
        completionHandler: (MutableList<Wordpress.Result>) -> Unit
    ) {
        data.forEach { item ->
            array.add(item)
        }
        completionHandler.invoke(array)
    }
}
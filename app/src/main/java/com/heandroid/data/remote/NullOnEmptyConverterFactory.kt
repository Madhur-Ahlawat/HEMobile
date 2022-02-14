package com.heandroid.data.remote

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


class NullOnEmptyConverterFactory : Converter.Factory(){
    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation?>?,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val delegate: Converter<ResponseBody, *> =
            retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
        return label@ Converter { body: ResponseBody ->
            //if (body.contentLength()<=0) return@Converter null
            if(body.source().exhausted()) return@Converter null
            delegate.convert(body)
        } as Converter<ResponseBody, Any?>
    }
}
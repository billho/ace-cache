package com.ace.cache.parser.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.ace.cache.parser.ICacheResultParser;
import com.alibaba.fastjson.JSONObject;

/**
 * 默认缓存结果解析类
 *
 * @author wanghaobin
 * @description
 * @date 2017年5月18日
 * @since 1.7
 */
public class DefaultResultParser implements ICacheResultParser {

    @Override
    public Object parse(String value, Type type, Class<?>... origins) {
        Object result = null;
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (((Class) rawType).isAssignableFrom(List.class)) {
//                result = JSON.parseArray(value, (Class) parameterizedType.getActualTypeArguments()[0]);
                //fix 多层泛型引起的bug
                if (parameterizedType.getActualTypeArguments()[0] instanceof ParameterizedType) {
                    if(JSONObject.class.equals(((ParameterizedType)(parameterizedType.getActualTypeArguments()[0])).getActualTypeArguments()[0])){
                        result = JSON.parseArray(value,(Class)((ParameterizedType)(parameterizedType.getActualTypeArguments()[0])).getRawType());
                    }
                    else {
                        result = JSON.parseArray(value, parameterizedType.getActualTypeArguments());
                    }
                }
                else {
                    result = JSON.parseArray(value, (Class) parameterizedType.getActualTypeArguments()[0]);
                }
            }
        } else if (origins == null) {
            result = JSON.parseObject(value, (Class) type);
        } else {
            result = JSON.parseObject(value, origins[0]);
        }
        return result;
    }
}

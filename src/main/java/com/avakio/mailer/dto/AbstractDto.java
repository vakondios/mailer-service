package com.avakio.mailer.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract class for the DataTransferObjects
 */

public class AbstractDto implements Serializable {

    public static  <T extends AbstractDto> T getInstance(Class<T> clazz) {
        try {
            Constructor<?> ctor = clazz.getConstructor();
            return (T) ctor.newInstance();
        } catch (Exception ex) {
            return null;
        }
    }

    public static <T extends AbstractDto> T  getInstance(Class<T> clazz,String json) {
        try{
            Map<String,Object> properties  = jsonToMap(json);
            BeanWrapper object = new BeanWrapperImpl(getInstance(clazz));
            assert properties != null;
            for (Map.Entry<String, Object> property : properties.entrySet()) {
                object.setPropertyValue(property.getKey(), property.getValue());
            }
            return (T) object.getWrappedInstance();

        } catch (Exception ex) {
            return null;
        }
    }

    public  static Map<String,Object> jsonToMap(String json){
        try{
            return new ObjectMapper().readValue(json, Map.class);
        } catch (IOException ex) {
            return null;
        }
    }

    private String getClassname() {
        Class<?> enclosingClass = getClass().getEnclosingClass();
        return Objects.requireNonNullElseGet(enclosingClass, this::getClass).getName();
    }

    private  Map<String,Object> jsonStringToMap(String json){
        try{
            return new ObjectMapper().readValue(json, Map.class);
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Returns the class to Json String
     * @return Json String
     */
    public  String toJson(){
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    /**
     * Returns a new instance
     * @param <T> class
     * @return new instance
     */
    public <T extends AbstractDto> T  newInstance(){
        String className = getClassname();
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor();
            return (T) ctor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    /**
     * Returns a new instance from json string
     * @param json String
     * @param <T> class
     * @return new instance
     */
    public <T extends AbstractDto> T  fromJson(String json) {
        Map<String,Object> properties  = jsonStringToMap(json);
        BeanWrapper object = new BeanWrapperImpl(newInstance());
        assert properties != null;
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            object.setPropertyValue(property.getKey(), property.getValue());
        }
        return (T) object.getWrappedInstance();
    }

    /**
     * Returns a new instance from map
     * @param properties String
     * @param <T> class
     * @return new instance
     */
    public <T extends AbstractDto> T  fromMap( Map<String,Object> properties) {
        BeanWrapper object = new BeanWrapperImpl(newInstance());
        assert properties != null;
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            object.setPropertyValue(property.getKey(), property.getValue());
        }
        return (T) object.getWrappedInstance();
    }

    /**
     * Copy by value the object
     * @param <T>
     * @return
     */
    public <T extends AbstractDto> T copyByValue(){
        Object obj = fromMap( new ObjectMapper().convertValue(this, Map.class));
        return (T) obj;
    }

    /**
     * Returns a new instance from json string
     * @param json String
     * @param <T> class
     * @return new instance
     */
    public <T extends AbstractDto> T  fromJson2(String json) {
        Object object = newInstance();
        try {
            Map<String,Object> map = jsonStringToMap(json);
            assert map != null;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey().trim();
                Field field = object.getClass().getDeclaredField(key);
                field.setAccessible(true);
                field.set(object, entry.getValue());
            }
            return (T) object;

        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Compares objects according to the fields' values
     * @param o
     * @return
     */
    public boolean  equalByValue(Object o) {
        try {
            String strA= new ObjectMapper().writeValueAsString(this);
            String strB= new ObjectMapper().writeValueAsString(o);
            return  strA.equals(strB);
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}

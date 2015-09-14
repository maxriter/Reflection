package service;

import annotations.CustomDateFormat;
import annotations.JsonValue;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Json {

    public String toJson(Object obj) throws IllegalAccessException, InstantiationException {
        boolean isFirst = true;
        String result = "";
        String name;
        String value;
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            JsonValue jsonValue = field.getAnnotation(JsonValue.class);
            CustomDateFormat customDateFormat = field.getAnnotation(CustomDateFormat.class);
            if (field.get(obj) != null) {
                if (isFirst == false) {
                    result += ",";
                }
                if (jsonValue != null) {
                    name = jsonValue.name();
                    value = (String) field.get(obj);
                    result += "\"" + name + "\"" + ":" + "\"" + value + "\"";
                    continue;
                }
                if (customDateFormat != null) {
                    try {
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(customDateFormat.format());
                        value = dateTimeFormatter.format((LocalDate) field.get(obj));
                        name = field.getName();
                        result += "\"" + name + "\":\"" + value + "\"";
                    } catch (IllegalArgumentException e) {
                        result += "\"" + field.getName().toString() + "\":\"" + field.get(obj).toString() + "\"";
                    } finally {
                        continue;
                    }
                }
                result += "\"" + field.getName().toString() + "\":\"" + field.get(obj).toString() + "\"";
                isFirst = false;
            }
        }
        return "{" + result + "}";
    }

    public <T> T fromJson(String json, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        Object res = clazz.newInstance();
        Map<String, String> map = parseJson(json, res);
        for (Field field : clazz.getDeclaredFields()) {
            if (map.containsKey(field.getName())) {
                Object inject = map.get(field.getName());
                if ((field.getAnnotation(CustomDateFormat.class) != null) || (field.getType().getSimpleName().equals("LocalDate"))) {
                    inject = LocalDate.parse(inject.toString());
                }
                field.setAccessible(true);
                field.set(res, inject);
            }
        }
        return (T) res;
    }

    private Map<String, String> parseJson(String str, Object obj) throws IllegalAccessException, InstantiationException {
        String[] arr = str.substring(1, str.length() - 1).split(",");
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < arr.length; i++) {
            String[] field = arr[i].split(":");
            String key = field[0].substring(1, field[0].length() - 1);
            String value = field[1].substring(1, field[1].length() - 1);
            map.put(key, value);
        }
        return map;
    }
}

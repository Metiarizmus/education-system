package com.nikolai.education.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {
    /**
     *Save properties
     */
    void set(String key, Object value, long time);

    /**
     *Save properties
     */
    void set(String key, Object value);

    /**
     *Get properties
     */
    Object get(String key);

    /**
     *Delete attribute
     */
    Boolean del(String key);

    /**
     *Batch delete attribute
     */
    Long del(List<String> keys);

    /**
     *Set expiration time
     */
    Boolean expire(String key, long time);

    /**
     *Get expiration time
     */
    Long getExpire(String key);

    /**
     *Determine whether the attribute exists
     */
    Boolean hasKey(String key);

    /**
     *Increment by Delta
     */
    Long incr(String key, long delta);

    /**
     *Decrease by Delta
     */
    Long decr(String key, long delta);

    /**
     *Get the attributes in the hash structure
     */
    Object hGet(String key, String hashKey);

    /**
     *Put an attribute into the hash structure
     */
    Boolean hSet(String key, String hashKey, Object value, long time);

    /**
     *Put an attribute into the hash structure
     */
    void hSet(String key, String hashKey, Object value);

    /**
     *Get the whole hash structure directly
     */
    Map<Object, Object> hGetAll(String key);

    /**
     *Set the whole hash structure directly
     */
    Boolean hSetAll(String key, Map<String, Object> map, long time);

    /**
     *Set the whole hash structure directly
     */
    void hSetAll(String key, Map<String, Object> map);

    /**
     *Delete attributes in hash structure
     */
    void hDel(String key, Object... hashKey);

    /**
     *Determine whether the attribute exists in the hash structure
     */
    Boolean hHasKey(String key, String hashKey);

    /**
     *Incremental attributes in hash structure
     */
    Long hIncr(String key, String hashKey, Long delta);

    /**
     *Attribute decrement in hash structure
     */
    Long hDecr(String key, String hashKey, Long delta);

    /**
     *Get set structure
     */
    Set<Object> sMembers(String key);

    /**
     *Adding attributes to the set structure
     */
    Long sAdd(String key, Object... values);

    /**
     *Adding attributes to the set structure
     */
    Long sAdd(String key, long time, Object... values);

    /**
     *Is it a property in set
     */
    Boolean sIsMember(String key, Object value);

    /**
     *Get set structure的长度
     */
    Long sSize(String key);

    /**
     *Delete attributes from set structure
     */
    Long sRemove(String key, Object... values);

    /**
     *Gets the properties in the list structure
     */
    List<Object> lRange(String key, long start, long end);

    /**
     *Gets the length of the list structure
     */
    Long lSize(String key);

    /**
     *Get the properties in the list according to the index
     */
    Object lIndex(String key, long index);

    /**
     *Adding attributes to the list structure
     */
    Long lPush(String key, Object value);

    /**
     *Adding attributes to the list structure
     */
    Long lPush(String key, Object value, long time);

    /**
     *Batch adding attributes to the list structure
     */
    Long lPushAll(String key, Object... values);

    /**
     *Batch adding attributes to the list structure
     */
    Long lPushAll(String key, Long time, Object... values);

    /**
     *Remove property from list structure
     */
    Long lRemove(String key, long count, Object value);
}

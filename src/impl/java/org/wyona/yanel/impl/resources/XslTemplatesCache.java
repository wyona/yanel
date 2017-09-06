package src.impl.java.org.wyona.yanel.impl.resources;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.transform.Templates;

import org.apache.log4j.Logger;

/**
 * This cache uses the ReentrantReadWriteLock from the java.util.concurrent.locks package instead of using synchronized code sections. 
 * Reason to use ReentrantReadWriteLock : we expect many concurrent Read Threads and only very few write threads. 
 * So we want to allow multiple read threads in parallel in order to improve performance.
 * 
 * @author baszero
 */
public class XslTemplatesCache {
    protected static Logger log = Logger.getLogger(XslTemplatesCache.class);

    private static volatile Map<String, Templates> templatesCache = new HashMap<String, Templates>();
    private static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock(Boolean.TRUE); // true = fair policy, order of lock acquisition is preserved
    private static final Lock r = rwl.readLock();
    private static final Lock w = rwl.writeLock();

    public static Templates get(String key) {
        r.lock(); // here it only waits if another thread is writing to the cache
        try {
            return templatesCache.get(key);

        } finally {
            r.unlock();
        }
    }
    
    public static Templates put(String key, Templates value) {
        w.lock(); // this thread waits until all preceding read threads have finished
        try {
            return templatesCache.put(key, value);

        } finally {
            w.unlock();
        }
    }

    public static void clear() {
        w.lock();
        try {
            templatesCache.clear();
        } finally {
            w.unlock();
        }
    }
    
    public static void clearSingle(String key) {
        if (key != null) {
            w.lock();
            try {
                templatesCache.remove(key);
            } finally {
                w.unlock();
            }
        }
    }
    
    /**
     * @return number of entries
     */
    public static int size() {
        int result = -1;
        r.lock();
        try {
            result = templatesCache.size();
        } finally {
            r.unlock();
        }
        return result;
    }
    
    public static Set<String> getKeys() {
        Set<String> result = null;
        r.lock();
        try {
            result = templatesCache.keySet();
        } finally {
            r.unlock();
        }
        return result;
    }

}
package dev.m7mqd.regions.flag;


import java.util.Objects;

public interface Flag {
    Key toKey();


    interface Key{
        static Key fromString(String key){
            return new KeyImpl(key);
        }
    }

    class KeyImpl implements Key{
        private final String key;

        public KeyImpl(String key) {
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            KeyImpl key1 = (KeyImpl) o;
            return Objects.equals(key, key1.key);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key);
        }

        @Override
        public String toString() {
            return this.key;
        }
    }
}

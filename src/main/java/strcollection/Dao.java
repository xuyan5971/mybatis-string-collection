package strcollection;

import java.util.Collection;
import java.util.List;
import java.util.Map;

interface Dao {
    Map<String, Object> selectStringsOk();
    Map<String, Object> selectWrongColumnMapping();
    Map<String,Object> selectNoJavaType();
}

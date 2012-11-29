package crono.type;

import crono.InterpreterException;

import java.util.ArrayList;

public class CronoVector extends CronoArray {
    public static final TypeId TYPEID = new TypeId(":vector",
                                                   CronoVector.class,
                                                   CronoArray.TYPEID);
    private static final String _rank_dim_mismatch =
        "Wrong number of dimensions to Array of rank %d: %d";
    private static final String _data_size_mismatch =
        "Data size (%d) does not match Array size: %d";
    private static final String _inv_rank =
        "Array Rank must be a positive, non-zero integer";
    
    protected ArrayList<CronoType> data;
    protected int size;
    protected TypeId type;
    
    public CronoVector(int size) {
        this(size, CronoType.TYPEID);
    }
    
    public CronoVector(int size, TypeId accept) {
        super(accept);
        
        this.type = new NestedTypeId(":vector", CronoVector.class,
                                     new TypeId[]{accept}, TYPEID);
        
        this.size = size;
        this.data = new ArrayList<CronoType>(size);
        for(int i = 0; i < size; ++i) {
            this.data.add(i, Nil.NIL);
        }
    }
    public CronoVector(CronoType[] data) {
        this(data, CronoType.TYPEID);
    }
    public CronoVector(CronoType[] data, TypeId accept) {
        super(accept);
        this.type = new NestedTypeId(":vector", CronoVector.class,
                                     new TypeId[]{accept}, TYPEID);
        this.data = new ArrayList<CronoType>();
        for(int i = 0; i < data.length; ++i) {
            this.data.add(data[i]);
        }
        this.size = data.length;
    }
    
    public int size() {
        return size;
    }
    
    public CronoType get(int n) {
        if(n >= size || n < 0) {
            throw new InterpreterException("Array index out of bounds");
        }
        return data.get(n);
    }
    public CronoType put(int n, CronoType item) {
        if(n >= size || n < 0) {
            throw new InterpreterException("Array index out of bounds");
        }
        data.set(n, item);
        return item;
    }
    public CronoType append(CronoType item) {
        data.add(item);
        size++;
        return item;
    }
    public CronoType insert(CronoArray array, int pos) {
        if(!(array instanceof CronoVector)) {
            throw new InterpreterException("insert: bleh");
        }
        
        data.addAll(pos, ((CronoVector)array).data);
        size = data.size();
        return this;
    }
    public CronoType concat(CronoArray array) {
        if(!(array instanceof CronoVector)) {
            throw new InterpreterException("concat: bleh");
        }
        
        data.addAll(((CronoVector)array).data);
        size = data.size();
        return this;
    }
    
    public TypeId typeId() {
        return type;
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for(int i = 0; i < size - 1; ++i) {
            builder.append(data.get(i).repr());
            builder.append(", ");
        }
        builder.append(data.get(size - 1).repr());
        builder.append("]");
        return builder.toString();
    }
    
    public boolean equals(Object o) {
        if(!(o instanceof CronoVector) || ((CronoVector)o).size() != size) {
            return false;
        }
        
        CronoVector vec = (CronoVector)o;
        for(int i = 0; i < size; ++i) {
            if(!(vec.get(i).equals(data.get(i)))) {
                return false;
            }
        }
        return true;
    }
}
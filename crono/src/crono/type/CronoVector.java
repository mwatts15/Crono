package crono.type;

import crono.InterpreterException;

public class CronoVector extends CronoArray {
    public static final TypeId TYPEID = new TypeId(":array", CronoArray.class);
    private static final String _rank_dim_mismatch =
	"Wrong number of dimensions to Array of rank %d: %d";
    private static final String _data_size_mismatch =
	"Data size (%d) does not match Array size: %d";
    private static final String _inv_rank =
	"Array Rank must be a positive, non-zero integer";
    
    protected CronoType[] data;
    protected int[] dimensions;
    protected int rank, size;
    protected TypeId type;
    
    public CronoVector(int rank, int[] dimensions) {
	this(rank, dimensions, CronoType.TYPEID);
    }
    
    public CronoVector(int rank, int[] dimensions, TypeId accept) {
	super(accept);
	
	if(rank <= 0) {
	    throw new InterpreterException(_inv_rank);
	}
	this.rank = rank;
	this.type = new NestedTypeId(":vector", CronoVector.class,
				     new TypeId[]{accept}, TYPEID);
	
	if(dimensions.length != rank) {
	    throw new InterpreterException(_rank_dim_mismatch,
					   dimensions.length, rank);
	}
	
	this.dimensions = dimensions;
	size = 1;
	for(int i = 0; i < dimensions.length; ++i) {
	    size *= dimensions[i];
	}
	
	this.data = new CronoType[size];
    }
    
    public CronoVector(int rank, int[] dimensions, TypeId accept,
		      CronoType[] data)
    {
	this(rank, dimensions, accept);
	
	if(data.length != size) {
	    throw new InterpreterException(_data_size_mismatch, data.length,
					   size);
	}
	
	for(int i = 0; i < size; ++i) {
	    this.data[i] = data[i];
	}
    }
    
    public int rank() {
	return rank;
    }
    public int dim(int n) {
	if(n > dimensions.length) {
	    throw new InterpreterException("rank mismatch");
	}
	return dimensions[n];
    }
    private int offset(int[] n) {
	int pos = n[n.length - 1];
	int step = dimensions[n.length - 1];
	for(int i = n.length - 2; i >= 0; --i) {
	    if(n[i] >= dimensions[i]) {
		throw new InterpreterException("Vector index out of bounds");
	    }
	    pos += step * n[i];
	    step *= dimensions[i];
	}
	return pos;
    }
    public CronoType get(int[] n) {
	return data[offset(n)];
    }
    public CronoType put(int[] n, CronoType item) {
	data[offset(n)] = item;
	return item;
    }
    
    public TypeId typeId() {
	return type;
    }
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Not implemented =D");
	return builder.toString();
    }
}
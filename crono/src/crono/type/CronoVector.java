package crono.type;

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
    protected TypeId accept;
    
    public CronoVector(int rank, int[] dimensions, TypeId accept,
		      CronoType[] data) {
	this(rank, dimensions, accept);

	if(data.length != size) {
	    throw new RuntimeException(String.format(_data_size_mismatch,
						     data.length, size));
	}
	
	for(int i = 0; i < size; ++i) {
	    this.data[i] = data[i];
	}
    }
    
    public CronoVector(int rank, int[] dimensions, TypeId accept) {
	if(rank <= 0) {
	    throw new RuntimeException(_inv_rank);
	}
	this.accept = accept;
	this.rank = rank;
	
	if(dimensions.length != rank) {
	    throw new RuntimeException(String.format(_rank_dim_mismatch,
						     dimensions.length, rank));
	}
	
	this.dimensions = dimensions;
	size = 1;
	for(int i = 0; i < dimensions.length; ++i) {
	    size *= dimensions[i];
	}
	
	this.data = new CronoType[size];
    }
    
    public CronoVector(int rank, int[] dimensions) {
	this(rank, dimensions, CronoType.TYPEID);
    }
    
    public int rank() {
	return rank;
    }
    public int dim(int n) {
	return dimensions[n];
    }
    public CronoType get(int[] n) {
	if(n.length != dimensions.length) {
	    throw new RuntimeException("Wrong number of indices");
	}
	return Nil.NIL; /*< TODO: Finish this */
    }
    
    public TypeId typeId() {
	return CronoVector.TYPEID;
    }
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Not implemented =D");
	return builder.toString();
    }
}
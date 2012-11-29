package crono.type;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import crono.Visitor;

public class Cons extends CronoType implements Iterable<CronoType> {
    public static final TypeId TYPEID = new TypeId(":cons", Cons.class,
                                                   CronoType.TYPEID);
    
    private class ConsIterator implements Iterator<CronoType> {
        private Cons cell;
        private boolean more;
        
        public ConsIterator(Cons cell) {
            this.more = (cell.cdr instanceof Cons);
            this.cell = cell;
        }
        
        public boolean hasNext() {
            return (cell != null) && ((more && cell.car != null) ||
                                      cell.cdr != null);
        }
        
        public CronoType next() {
            if(cell == null) {
                throw new NoSuchElementException();
            }
            
            if(more) {
                CronoType ret = cell.car;
                if(cell.cdr instanceof Cons) {
                    cell = (Cons)(cell.cdr);
                }else {
                    more = false;
                }
                return ret;
            }
            
            CronoType ret = cell.cdr;
            cell = null;
            return ret;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    public static Cons fromList(List<CronoType> args) {
        if(args.size() <= 0) {
            return Nil.NIL;
        }
        
        Cons head = new Cons();
        Cons curr = head;
        Iterator<CronoType> iter = args.iterator();
        curr.car = iter.next();
        while(iter.hasNext()) {
            curr.cdr = new Cons();
            curr = (Cons)curr.cdr;
            curr.car = iter.next();
        }
        curr.cdr = Nil.NIL;
        return head;
    }
    
    protected CronoType car, cdr;
    
    protected Cons() {
        this.car = null;
        this.cdr = null;
    }
    
    public Cons(CronoType car, CronoType cdr) {
        this.car = car;
        this.cdr = cdr;
    }
    
    public Cons cons(CronoType car) {
        return new Cons(car, this);
    }
    
    public CronoType car() {
        return (car == null) ? Nil.NIL : car;
    }
    public CronoType cdr() {
        return (cdr == null) ? Nil.NIL : cdr;
    }
    
    public Iterator<CronoType> iterator() {
        return new ConsIterator(this);
    }
    
    public CronoType accept(Visitor v) {
        return v.visit(this);
    }
    
    public TypeId typeId() {
        return TYPEID;
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        
        Cons next = this;
        while(next != null) {
            builder.append(next.car);
            if(next.cdr instanceof Cons) {
                if(next.cdr == Nil.NIL) {
                    next = null;
                }else {
                    builder.append(" ");
                    next = (Cons)(next.cdr);
                }
            }else {
                builder.append(" . ");
                builder.append(next.cdr);
                next = null;
            }
        }
        
        builder.append(")");
        return builder.toString();
    }
    
    public List<CronoType> toList() {
        List<CronoType> list = new LinkedList<CronoType>();
        Cons next = this;
        while(next != null) {
            list.add(next.car);
            if(next.cdr instanceof Cons) {
                if(next.cdr == Nil.NIL) {
                    next = null;
                }else {
                    next = (Cons)(next.cdr);
                }
            }else {
                list.add(next.cdr);
                next = null;
            }
        }
        return list;
    }
}

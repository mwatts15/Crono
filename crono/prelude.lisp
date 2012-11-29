%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% boolean functions
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
(define not (\ (bool) (if (= bool nil) #t #f)))

(define and (\ (a b) (if a b nil)))

(define or (\ (a b) (if a t b)))

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% higher order functions
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
(define map (\ (f l) (if (= l nil) nil (cons (f (car l)) (map f (cdr l))))))

(define _foldl (\ (f l s) (if (= l nil) s (_foldl f (cdr l) (f s (car l))))))

(define foldl (\ (f l) (if (= l nil) nil (_foldl f (cdr l) (car l)))))

(define _foldr (\ (f l s) (if (= l nil) s (f (car l) (_foldr f (cdr l) s)))))

(define foldr (\ (f l) (if (= l nil) nil (_foldr f (cdr l) (car l)))))

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% math functions
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
(define ! (\ (n) (if (= n 0) 1 (* (! (- n 1)) n))))

(define mod (\ (x y) (if (< x y) x (mod (- x y) y))))

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% combinators
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%identity combinator
(define <I> (\ (x) x))
  
%Constant combinator
(define <K> (\ (x y) x))

%General application combinator
(define <S> (\ (f g p) (f p (g p))))
  
%Function composition combinator
(define <B> (\ (f g x) (f (g x))))

%
(define <C> (\ (f x y) (f y x)))

%(define <Y> (f)
%  (f (<Y> f)) )
%Cannata's incorrect definition of Y combinator

%Recursive function 
(define <Z> (\ (f)
    ((\ (x) (f (\ (v) ((x x) v))))
     (\ (x) (f (\ (v) ((x x) v)))))))

(define <Y> (\ (f)
  (\ (x) (f (x x))) (\ (x) (f (x x))) ))
(define pred (\ (x) (- x 1)))
(define my_cond (\ (p f g x) (if (p x) (f x) (g x))))
(define fapp (\ (x y) ((<S> (<K> (<S> <I>))   <K>) x y)))
(define pradd (\ (x z) (<Z> (<B> (my_cond ((=) 0) (<K> z)) (<B> (<S> (<B> (+) (<K> 1)) ) (<C> <B> pred))) x)))


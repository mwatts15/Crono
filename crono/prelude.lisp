%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% boolean functions
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
(define not (\ (bool) (if (= bool nil) #t #f)))

(define and (\ (a b) (if a b #f)))

(define or (\ (a b) (if a #t b)))

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% higher order functions
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
(defun map (f l) (if (= l nil) nil (cons (f (car l)) (map f (cdr l)))))

(defun _foldl (f l s) (if (= l nil) s (_foldl f (cdr l) (f s (car l)))))

(defun foldl (f l) (if (= l nil) nil (_foldl f (cdr l) (car l))))

(defun _foldr (f l s) (if (= l nil) s (f (car l) (_foldr f (cdr l) s))))

(defun foldr (\ (f l) (if (= l nil) nil (_foldr f (cdr l) (car l)))))

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% math functions
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
(defun ! (n) (if (= n 0) 1 (* (! (- n 1)) n)))

(defun mod (x y) (if (< x y) x (mod (- x y) y)))

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% combinators
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
(define pred (+ -1))
(define succ (+ 1))

%identity combinator
(define <I> (\ (x) x))
  
%Constant combinator
(define <K> (\ (x y) x))

%General application combinator
(define <S> (\ (f g x) ((f x) (g x))))
  
%Function composition combinator
(define <B> (\ (f g x) (f (g x))))
% (define <B> (<S> (<K> <S>) <K>))

%
(define <C> (\ (f x y) ((f y) x)))
% (define <C> (<S> (<S> (<K> (<S> (<K> <S>) <K>)) <S>) (<K> <K>)))

%Recursive function 
(define <Y> (\ (f)
    ((\ (x) (f (\ (v) ((x x) v))))
     (\ (y) (f (\ (w) ((y y) w)))))))
%(define <Y> (\ (f)
%  (\ (x) (f (x x))) (\ (x) (f (x x))) ))

(define <COND> (\ (p f g x) (if (p x) (f x) (g x))))

(defun pradd1 (x z)
  (<Y> (<B>
        (<COND> (= 0) (<K> z))
        (<B> (<S> (<B> + (<K> 1))) (<C> <B> pred)))
       x))

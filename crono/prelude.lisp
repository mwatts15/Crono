%%%%
% Base Predicates
%%%%
(define nil? (= Nil))
(define zero? (= 0))
(define t? (= #t))

(defun any?       (value) (= (typeof value) :any))
(defun array?     (value) (= (typeof value) :array))
(defun atom?      (value) (= (typeof value) :atom))
(defun char?      (value) (= (typeof value) :char))
(defun cons?      (value) (= (typeof value) :cons))
(defun float?     (value) (= (typeof value) :float))
(defun func?      (value) (= (typeof value) :func))
(defun int?       (value) (= (typeof value) :int))
(defun number?    (value) (= (typeof value) :number))
(defun primitive? (value) (= (typeof value) :primitive))
(defun string?    (value) (= (typeof value) :string))
(defun struct?    (value) (= (typeof value) :struct))
(defun type?      (value) (= (typeof value) :type))
(defun vector?    (value) (= (typeof value) :vector))

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% boolean functions
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
(define not (\ (bool) (if (= bool nil) #t #f)))

(define and (\ (a b) (if a b #f)))

(define or (\ (a b) (if a #t b)))

%%%%
% Extra predicates
%%%%
(defun boolean? (value) (or (nil? value) (t? value)))

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% higher order functions
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
(defun map (f l) (if (= l nil) nil (cons (f (car l)) (map f (cdr l)))))

(defun _foldl (f l s) (if (= l nil) s (_foldl f (cdr l) (f s (car l)))))

(defun foldl (f l) (if (= l nil) nil (_foldl f (cdr l) (car l))))

(defun _foldr (f l s) (if (= l nil) s (f (car l) (_foldr f (cdr l) s))))

(defun foldr (f l) (if (= l nil) nil (_foldr f (cdr l) (car l))))

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
(define <S> (\ (f g p) ((f p) (g p))))
  
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

(define my_cond (\ (p f g x) (if (p x) (f x) (g x))))

% (define prFoldr (\ (fn z x)
%  (<Y> (<B> (my_cond (= nil) (<K> z)) (<B> (<S> (<B> fn (car)) ) (<C> <B> cdr))) x)))
(defun pradd1 (x z)
  (<Y> (<B>
        (my_cond (= 0) (<K> z))
        (<B> (<S> (<B> + (<K> 1))) (<C> <B> pred)))
       x))

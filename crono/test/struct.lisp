(load prelude.lisp)

(struct Person name gender age)
(substruct Employee Person position (wage (+ 7 0.25)) boss)

(define give_raise (emp amt) (emp wage (+ (emp wage) amt)))
% A bit hackish, but it 'works'
(define troy Nil (newstruct Employee (name (' "Troy Varney"))
			    (gender (' "Male")) (age 21)
			    (position (' "Programmer")) (wage 20.25)
			    (boss (' "Guy"))))

(printstr "Employee Record:\n")
(printstr "Name:")
(let ((name ((troy) name))) (printstr name))
(printstr "\n")
(printstr "Age:")
(print ((troy) age))
(printstr "Wage:")
(print ((troy) wage))

(ns wonderland
  (:require [clojure.set :as s]))

(defn common-fav-foods [foods1 foods2]
  (let [food-set-1 (set foods1)
        food-set-2 (set foods2)
        common-foods (s/intersection food-set-1 food-set-2)]
    (str "common foods: " common-foods)))

(common-fav-foods '(:pao :banana :feijao :maçã) '(:pao :maçã))

(defn drinkable? [x]
  (= x :drinkable))

(drinkable? :drinkable)

(every? drinkable? [:drinkable :drinkable])

;; test if every element is the oposite of drinkable
(not-any? drinkable? [:poison :coke])

;; test if at lease one element is drinkable
(some #(= % :drinkable) [:drinkable :poison])

;; conditional
(defn grown [name direction]
  (if (= direction :small)
    (str name "is growing smaller")
    (str name "is growing bugger")))

(grown "Joffily" :bigger)

;; use when WHEN you are testing the truth of thing
(defn is-begin-bigger? [direction]
  (when (= direction :bigger)
    (str "You are going bigger!")))

(is-begin-bigger? :bigger)
(is-begin-bigger? :small)

;; use partial to compose values and create new function
(defn add [x y]
  (+ x y))

(defn add3 [x]
  ((partial add 3) x))

(add3 10)


;; Destructuring things


(let [[color size] ["blue" :small]]
  (str "The color " color " is " size))

;; there some more things about destructuring but it is boring,
;; I promisse, in the future I will back to the topic

;; Laziness
;; According the book, clojure can handle infinite lists, lets see that.

(take 5 (range)) ;; the default for (range) is infinity
(take-last 10 (range 100))

(count
 (take 10
       (repeat "hehe")))

;; generating random numbers
(rand-int 10)

;; repeate random number
(repeat 5 (rand-int 10))
;; => (9 9 9 9 9)

;; But if we need different numbers instead?
(repeatedly 5 #(rand-int 10))

;; repeat function just repeat a value over and over,
;; the repeatedly function takes a function and evaluate
;; it over and over then we get our random integers

(take 10
      (repeatedly #(rand-int 100)))
;;=> (31 73 42 56 13 71 44 64 1 20)

;; another function to work with laziness is `cycle`,
;; it takes an collection as argument and repeate its
;; items over and over

(take 5 (cycle [:primeiro :segundo]))
;; => (:primeiro :segundo :primeiro :segundo :primeiro)


;; RECURSION

(def adjs ["normal" "too small" "too big" "swimming"])

(defn alice-is [in out]
  (if (empty? in)
    out             ;; returns the full out list
    (alice-is
     (rest in)   ;; take the rest of in and call alice-is with
     (conj out (str "Alice is " (first in)))))) ;; append the first element to out vector

(alice-is adjs [])

;; Another example of recursion. We did a countdown
(defn countdown [n]
  (if (= n 0)
    n
    (countdown (- n 1))))

(countdown 3)
;; => 0


;; As we learning in the CS classes, recursion add calls
;; to the stack then if we add more calls than the stack supports
;; it will crash
(countdown 10000)
;; Syntax error (StackOverflowError) compiling at (main.clj:2:1).


;; Clojure uses the recur function to avoid that.

(defn countdown-stack-clean [n]
  (if (= n 0)
    n
    (recur (- n 1))))

;; recur calls the function `countdown-stack-clean` again and again
;; until it finishs at the if clause.

(countdown-stack-clean 10000)
;; => 0

; The recur is how Clojure avoids this stack consumption, 
; by evaluating the function arguments and defining a position 
; where the call is going to “jump” back to the recur‐ sion point. 
; This way, it only needs one stack at a time. 

; In this case, the recursion point is the function itself, 
; because there is no loop. In general, always use 
; recur when you are doing recursive calls

;; shape of data with map, filter and reduce.
; Map doesn't change the shape of data, if we pass a list with 3
; items the result of map will contain 3 items.
; Reduce can change the shape, for example

(reduce + [1 2 3 5])
; => 11
(reduce #(+ %1 (* %2 %2)) [1 2 3])
; => 14


(for [animal [:mouse :duck]]
  (str (name animal)))
; => ("mouse" "duck")

;; when we use more than one collection the for statment
;; do a nested for over the collections
(for [animal [:mouse :ducl]
      color  [:red :blue]]
  (str (name animal) (name color)))
;; => ("mousered" "mouseblue" "duclred" "duclblue")


;; we can use let inside for.
(for [animal [:mouse :cow]
      color [:red :blue :yellow]
      :let [animal-str (str "animal-" (name animal))
            color-str (str "color-" (name color))
            display-str (str animal-str "-" color-str)]
      :when (= color :blue)]
  display-str)

;=> ("animal-mouse-color-blue" "animal-cow-color-blue")


(partition 3 [1 2 3 4 5 6 7 8 9])
; => ((1 2 3) (4 5 6) (7 8 9))

(partition-by #(= 6 %) [1 2 3 4 5 6 7 8 9 10])
; => ((1 2 3 4 5) (6) (7 8 9 10))


;; Clojure has 4 data structures and their are immutable
;; list, sets, vectors and maps. But the real world
;; has mutable data then we need to represent it in any
;; form. Clojure uses `atom` to do it. Lets check.

(def who-atom (atom :butterfly))
; who-atom
; #object[clojure.lang.Atom 0x6dee8648 {:status :ready, :val :butterfly}]
; @who-atom
; :butterfly

; To change the atom value we can use the `reset!` and `swap!`
(reset! who-atom :chrysalis)
; => :chrysalis

(def who-atom (atom :caterpillar))

(defn change [state]
  (case state
    :caterpillar :chrysalis
    :chrysalis :butterfly
    :butterfly))

(swap! who-atom change)
; => :chrysalis
(swap! who-atom change)
; => :butterfly
(swap! who-atom change)
; => :butterfly

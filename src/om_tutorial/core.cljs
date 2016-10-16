(ns om-tutorial.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(def app-state 
  (atom 
    {:app/title "Animals"
     :animals/list
     [[1 "Ant"] [2 "Antelope"] [3 "Bird"] [4 "Cat"] [5 "Dog"]
      [6 "Lion"] [7 "Mouse"] [8 "Monkey"] [9 "Snake"] [10 "Zebra"]]
     :names/list
     [[1 "Name1"] [2 "Name2"] [3 "Name3"] [4 "Name4"] [5 "Name5"]
      [6 "Name6"] [7 "Name7"] [8 "Name8"] [9 "Name9"] [10 "Name1"]]}))



(defmulti read (fn [env key params] key))

(defmethod read :default
  [{:keys [state] :as env} key params]
  (let [st @state]
      (if-let [[_ value] (find st key)]
        {:value value}
        {:value :not-found})))

(defmethod read :animals/list
  [{:keys [state] :as env} key {:keys [start end]}]
  {:value (subvec (:animals/list @state) start end)})

(defmethod read :names/list
  [{:keys [state] :as env} key {:keys [start end]}]
  {:value (subvec (:names/list @state) start end)})   

(defui AnimalsList
  static om/IQueryParams
  (params [this]
      {:start 0 :end 10})
  static om/IQuery
  (query [this]
    '[:app/title (:names/list {:start ?start :end ?end})])
  Object
  (render [this]
    (let [{:keys [app/title names/list]} (om/props this)]
      (dom/div nil
        (dom/h2 nil title)
        (apply dom/ul nil
            (map
              (fn [[i name]]
                (dom/li nil (str i ". " name)))
              list))))))

; (defn mutate [{:keys [state] :as env} key params]
;   (if (= 'increment key)
;     {:value {:keys [:count]}
;      :action #(swap! state update-in [:count] inc)}
;     {:value :not-found}))        

; (defui Counter
;   static om/IQuery
;   (query [this]
;       [:count])
;   Object
;   (render [this]
;     (let [{:keys [count]} (om/props this)]
;       (dom/div nil
;         (dom/span nil (str "Count: " count))
;         (dom/button
;           #js {:onClick
;                 (fn [e] (om/transact! this '[(increment)]))}
;           "Click me!")))))

(def reconciler
  (om/reconciler 
    {:state app-state
     :parser (om/parser {:read read})}))

(om/add-root! reconciler
  AnimalsList (gdom/getElement "app"))

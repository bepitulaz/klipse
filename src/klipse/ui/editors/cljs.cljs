(ns klipse.ui.editors.cljs
  (:require
    [gadjett.core :as gadjett :refer-macros [dbg]]
    [clojure.string :as string :refer [blank?]]
    [klipse.ui.editors.editor :as editor]
    [klipse.ui.editors.common :refer [handle-events]]
    [klipse.utils :refer [url-parameters]] 
    [om.next :as om :refer-macros [defui]]
    [om.dom :as dom]))

(def config-editor 
  {:lineNumbers true
   :matchBrackets true 
   :mode "clojure"
   :scrollbarStyle "overlay"})

(def placeholder-editor
  (str
    ";; Write your clojurescript expression \n" 
    ";; and press Ctrl-Enter or wait for 3 sec to experiment the magic..."))

(defn process-input [component s]
  (when-not (blank? s)
    (om/transact! component 
                  [`(input/save   {:value ~s})
                   `(cljs/compile {:value ~s})
                   `(js/eval      {:value ~s})
                   `(clj/eval     {:value ~s})])))

(defn init-input [component s]
  (om/transact! component
                  [(list 'input/save     {:value s})]))

(defn init-editor [compiler]
  (let [my-editor (editor/create "code-cljs" config-editor)]
    (handle-events my-editor
                   {:idle-msec 3000
                    :on-should-eval #(process-input compiler (editor/get-value my-editor))})))

(defui Cljs-editor
  
  static om/IQuery
  (query [this] 
    '[:input])
  
  Object

  (componentDidMount [this]
    (init-editor this))

  (render [this]
    (let [input (:input (om/props this))]
      (dom/section #js {:className "cljs-editor"}
      (dom/textarea #js {:autoFocus true
                         :value input
                         :id "code-cljs"
                         :placeholder placeholder-editor})))))

(def cljs-editor (om/factory Cljs-editor))

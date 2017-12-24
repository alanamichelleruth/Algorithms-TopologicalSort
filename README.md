# Algorithms-TopologicalSort
Project for Algorithms and Data Structures-- write a list of people ordered so that no one appears in the list before anyone he/she is less smart than.

## Problem
Provide a RESTful service which accepts as a POST of JSON as a list of relationships [A, B] such that A is smarter than B.  
<br />Return in JSON the list sorted by smartness, with the smartest person first.
<br />Example input: “Einstein is smarter than Feynmann”, “Feynmann is smarter than Gell-Mann”, etc.)
<br />{ “inList” : [ { “smarter” : [ “Einstein”, “Feynmann” ] },            
<br />	{ “smarter” : [ “Feynmann”, “Gell-Mann” ] }, 
<br />{ “smarter” : [ “Gell-Mann”, “Thorne” ] }, 
<br />{ “smarter” : [ “Einstein”, “Lorentz” ] }, 
<br />{ “smarter” : [ “Lorentz”, “Planck” ] }, 
<br />{ “smarter” : [ “Hilbert”, “Noether” ] }, 
<br />{ “smarter” : [ “Poincare ”, “Noether” ] } ] } 
<br />Example output: 	
<br />{ “outList” : [ “Einstein”, “Feynmann”, “Gell-Mann”, “Thorne”, “Lorentz”, “Planck”,
 		“Hilbert”, “Poincare”, “Noether” ] } 
<br />Erroneous input (e.g. malformed JSON) should be handled gracefully.  

## Deliverable
An HTTP URL was available for the class project yet was destroyed upon completion. Users invoked a RESTful service with a tool such as curl or Postman.

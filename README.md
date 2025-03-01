# WORK IN PROGRESS

This branch is a WIP to test the possibility of deploying the Shopping Basket App 
onto a cluster. Doing so would leverage Akka to distribute the workload across different 
nodes at the item level. 

Intended implementation: 
* GKE 
* Terraform scripting for IAC 
* Akka Group Router for routing of workloads

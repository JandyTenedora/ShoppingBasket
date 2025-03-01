output "kubeconfig" {
  description = "The kubeconfig endpoint"
  value       = google_container_cluster.primary.endpoint
}
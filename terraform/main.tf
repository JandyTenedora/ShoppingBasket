provider "google" {
  project = var.project_id
  region  = var.region
}

resource "google_container_cluster" "primary" {
  name     = "shopping-basket-adthena"
  location = var.region

  initial_node_count = 3

  node_config {
    machine_type = "e2-micro"
  }

  autoscaling {
    min_node_count = 1
    max_node_count = 5
  }
}
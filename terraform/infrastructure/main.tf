terraform {
  backend "s3" {
    bucket  = var.s3_bucket_name
    encrypt = true
    key     = "AWS/${var.s3_bucket_name}}/terraform.tfstate"
    region  = var.region
  }
}

provider "aws" {}

resource "aws_default_vpc" "default_vpc" {}

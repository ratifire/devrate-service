terraform {
  backend "s3" {
    bucket  = "devrate-bucket-back-1"
    encrypt = true
    key     = "AWS/output-back-tstates/terraform.tfstate"
    region  = "eu-north-1"
  }
}

provider "aws" {}



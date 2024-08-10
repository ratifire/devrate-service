# terraform {
#   backend "s3" {
#     bucket  = "devrate-bucket-back-1"
#     encrypt = true
#     key     = "AWS/back-infrastructure-tstates/terraform.tfstate"
#     region  = "eu-north-1"
#   }
# }

provider "aws" {}

resource "aws_default_vpc" "default_vpc" {}

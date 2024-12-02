terraform {
  backend "s3" {}
}

provider "aws" {}

resource "aws_default_vpc" "default_vpc" {}

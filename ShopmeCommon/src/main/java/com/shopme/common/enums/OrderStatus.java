package com.shopme.common.enums;

public enum OrderStatus {

    NEW {
        @Override
        public String getDefaultDescription() {
            return "Order was placed by customer";
        }
    },
    CANCELED {
        @Override
        public String getDefaultDescription() {
            return "Order was placed by canceled";
        }
    },
    PROCESSING {
        @Override
        public String getDefaultDescription() {
            return "Order is being processed";
        }
    },
    PACKAGED {
        @Override
        public String getDefaultDescription() {
            return "Products were packaged";
        }
    },
    PICKED {
        @Override
        public String getDefaultDescription() {
            return "Shipper picked the package";
        }
    },
    SHIPPING {
        @Override
        public String getDefaultDescription() {
            return "Shipper is delivering the package";
        }
    },
    DELIVERED {
        @Override
        public String getDefaultDescription() {
            return "Customer received the products";
        }
    },
    RETURN_REQUESTED {
        @Override
        public String getDefaultDescription() {
            return "Order were requested to return";
        }
    },
    RETURNED {
        @Override
        public String getDefaultDescription() {
            return "Products were returned";
        }
    },
    PAID {
        @Override
        public String getDefaultDescription() {
            return "Customer has paid this order";
        }
    },
    REFUNDED {
        @Override
        public String getDefaultDescription() {
            return "Customer has been refunded";
        }
    };

    public abstract String getDefaultDescription();
}

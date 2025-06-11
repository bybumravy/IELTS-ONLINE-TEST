import React, { useState } from 'react';
import axios from 'axios';
import { Button, message, Modal } from 'antd';
import { WalletOutlined } from '@ant-design/icons';

const MomoPayment = ({ amount, orderInfo, onSuccess }) => {
    const [loading, setLoading] = useState(false);

    const handlePayment = async () => {
        try {
            setLoading(true);
            const response = await axios.post('http://localhost:8080/api/payment/create', {
                orderId: `ORDER_${Date.now()}`,
                amount: amount,
                orderInfo: orderInfo,
                extraData: ''
            });

            if (response.data.resultCode === 0) {
                // Redirect to MoMo payment page
                window.location.href = response.data.payUrl;
            } else {
                message.error('Failed to create payment: ' + response.data.message);
            }
        } catch (error) {
            message.error('Error creating payment: ' + error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Button
            type="primary"
            icon={<WalletOutlined />}
            loading={loading}
            onClick={handlePayment}
            style={{ backgroundColor: '#d82d8b' }}
        >
            Pay with MoMo
        </Button>
    );
};

export default MomoPayment; 
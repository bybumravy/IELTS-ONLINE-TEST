import { useState } from 'react';
import axios from 'axios';
import { Button, message, Modal } from 'antd';
import { WalletOutlined } from '@ant-design/icons';

const MomoPayment = ({ amount, orderInfo, onSuccess }) => {
    const [loading, setLoading] = useState(false);
    const orderId = `ORDER_${Date.now()}`;

    const handlePayment = async () => {
        try {
            setLoading(true);
            const response = await fetch(`http://localhost:8080/api/payment/create`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    orderId,
                    amount,
                    orderInfo,
                    extraData: '',
                    returnUrl: `http://localhost:5173/payment-callback?orderId=${orderId}`,
                    notifyUrl: `http://localhost:8080/api/payment/ipn`
                })
            });
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const data = await response.json();

            if (data.resultCode === 0) {
                // Redirect to MoMo payment page
                window.location.href = data.payUrl;
            } else {
                message.error('Failed to create payment: ' + data.message);
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
            icon={<WalletOutlined /> as React.ReactNode}
            loading={loading}
            onClick={handlePayment}
            style={{ backgroundColor: '#d82d8b' }}
        >
            Pay with MoMo
        </Button>
    );
};

export default MomoPayment; 
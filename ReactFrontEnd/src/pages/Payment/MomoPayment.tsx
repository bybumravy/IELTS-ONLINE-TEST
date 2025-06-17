import { useState } from 'react';
import axios from 'axios';
import { Button, message, Modal } from 'antd';
import { WalletOutlined } from '@ant-design/icons';

const MomoPayment = ({ amount, orderInfo, onSuccess }) => {
    const [loading, setLoading] = useState(false);
    const orderId = `ORDER_${Date.now()}`;

    const handlePayment = async () => {
        try {
            const response = await axios.post(`http://localhost:8080/api/momo/create`);
            const payUrl = response.data.payUrl;
            if (payUrl) {
                window.location.href = payUrl;
            } else {
                alert("Không nhận được URL thanh toán từ server.");
            }
        } catch (error) {
            console.error("Lỗi khi tạo thanh toán MoMo:", error);
            alert("Đã xảy ra lỗi khi tạo thanh toán.");
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
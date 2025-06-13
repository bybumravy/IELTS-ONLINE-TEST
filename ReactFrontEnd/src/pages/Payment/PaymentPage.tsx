import { useState } from 'react';
import { Card, Radio, Button, message } from 'antd';
import MomoPayment from './MomoPayment';

const packages = [
    { id: 'basic', name: 'Basic Package', price: 199000, description: 'Truy cập 1 tháng, luyện đề cơ bản' },
    { id: 'pro', name: 'Pro Package', price: 399000, description: 'Truy cập 3 tháng, bao gồm chiến lược nâng band' },
    { id: 'vip', name: 'VIP Package', price: 599000, description: 'Truy cập 6 tháng, kèm mentor hỗ trợ' },
];

const PaymentPage = () => {
    const [selectedPackage, setSelectedPackage] = useState<string | null>(null);
    const [paymentMethod, setPaymentMethod] = useState('momo');

    const handlePayment = () => {
        if (!selectedPackage) {
            message.warning('Vui lòng chọn gói học');
            return;
        }

        if (paymentMethod === 'momo') {
            // Chuyển sang component MoMo thanh toán
            document.getElementById('momo-button')?.click();
        } else if (paymentMethod === 'bank') {
            message.info('Vui lòng chuyển khoản ngân hàng tới STK 0123456789 - IELTS Center');
            // Có thể hiển thị thêm mã đơn hàng, QR chuyển khoản v.v.
        }
    };

    const selectedPkg = packages.find((pkg) => pkg.id === selectedPackage);

    return (
        <div style={{ maxWidth: 800, margin: '0 auto', padding: 20 }}>
            <h1>Chọn gói học</h1>
            <div style={{ display: 'flex', gap: 20, flexWrap: 'wrap' }}>
                {packages.map((pkg) => (
                    <Card
                        key={pkg.id}
                        title={pkg.name}
                        bordered
                        style={{
                            width: 240,
                            border: selectedPackage === pkg.id ? '2px solid #1890ff' : undefined,
                            cursor: 'pointer'
                        }}
                        onClick={() => setSelectedPackage(pkg.id)}
                    >
                        <p>{pkg.description}</p>
                        <strong>{pkg.price.toLocaleString()} VNĐ</strong>
                    </Card>
                ))}
            </div>

            <h2 style={{ marginTop: 30 }}>Chọn phương thức thanh toán</h2>
            <Radio.Group onChange={(e) => setPaymentMethod(e.target.value)} value={paymentMethod}>
                <Radio value="momo">Thanh toán bằng MoMo</Radio>
                <Radio value="bank">Chuyển khoản ngân hàng</Radio>
            </Radio.Group>

            <div style={{ marginTop: 30 }}>
                {paymentMethod === 'momo' && selectedPkg && (
                    <MomoPayment
                        amount={selectedPkg.price}
                        orderInfo={`Thanh toán gói ${selectedPkg.name}`}
                        onSuccess={() => message.success('Thanh toán thành công!')}
                    />
                )}

                <Button
                    id="momo-button"
                    type="primary"
                    onClick={handlePayment}
                    style={{ marginLeft: 10 }}
                >
                    Thanh toán
                </Button>
            </div>
        </div>
    );
};

export default PaymentPage;

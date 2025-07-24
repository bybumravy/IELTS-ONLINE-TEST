import React from 'react';
import {
    EmojiEvents as TrophyIcon,
    TrendingUp as TrendingIcon,
    Star as StarIcon,
} from '@mui/icons-material';
import type { TestHistory } from '../../services/historyService';
import { motion } from 'framer-motion';

interface HistoryStatsProps {
    items: TestHistory[];
}

const HistoryStats: React.FC<HistoryStatsProps> = ({ items }) => {
    const totalTests = items.length;
    const averageBand = items.reduce((acc, item) => acc + item.band, 0) / totalTests || 0;
    const highestBand = Math.max(...items.map(item => item.band), 0);

    const StatCard = ({
                          title,
                          value,
                          icon,
                          color,
                      }: {
        title: string;
        value: string;
        icon: React.ReactNode;
        color: string;
    }) => (
        <motion.div
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.98 }}
            className="flex flex-col items-center justify-center bg-gradient-to-br from-green-100 to-white rounded-2xl shadow-md p-6 cursor-pointer"
        >
            <motion.div
                className="text-4xl mb-3"
                style={{ color }}
                whileHover={{ rotate: [0, 10, -10, 0] }} // tạo hiệu ứng lắc nhẹ khi hover
                transition={{ duration: 0.6 }}
            >
                {icon}
            </motion.div>
            <div className="text-4xl font-extrabold text-green-700 mb-1">{value}</div>
            <div className="text-gray-600 font-medium text-sm">{title}</div>
        </motion.div>
    );

    return (
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
            <StatCard
                title="Tổng số bài thi"
                value={totalTests.toString()}
                icon={<TrophyIcon fontSize="inherit" />}
                color="#43A047"
            />
            <StatCard
                title="Band trung bình"
                value={averageBand.toFixed(1)}
                icon={<TrendingIcon fontSize="inherit" />}
                color="#388E3C"
            />
            <StatCard
                title="Band cao nhất"
                value={highestBand.toFixed(1)}
                icon={<StarIcon fontSize="inherit" />}
                color="#2E7D32"
            />
        </div>
    );
};

export default HistoryStats;

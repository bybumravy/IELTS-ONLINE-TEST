import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import CateSkill from "@/components/sections/CateSkill";
import MockTest from "@/components/sections/MockTest";

type SkillType = 'Listening' | 'Reading' | 'Writing' | 'Speaking' | 'All Skills';

const ListTestPage: React.FC = () => {
    const { skill } = useParams<{ skill?: string }>();
    const navigate = useNavigate();
    const [selectedSkill, setSelectedSkill] = useState<SkillType>('All Skills');

    useEffect(() => {
        if (skill) {
            const formattedSkill = skill.charAt(0).toUpperCase() + skill.slice(1).toLowerCase();
            if (['Listening', 'Reading', 'Writing', 'Speaking', 'All Skills'].includes(formattedSkill as SkillType)) {
                setSelectedSkill(formattedSkill as SkillType);
            }
        }
    }, [skill]);

    const handleSkillChange = (newSkill: SkillType) => {
        setSelectedSkill(newSkill);
        if (newSkill === 'All Skills') {
            navigate('/test');
        } else {
            navigate(`/test/${newSkill.toLowerCase()}`);
        }
    };

    return (
        <>
            <CateSkill onSkillChange={handleSkillChange} initialSkill={selectedSkill} />
            <MockTest selectedSkill={selectedSkill} />
        </>
    );
};

export default ListTestPage;
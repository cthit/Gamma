import React, { useEffect, useState } from "react";
import styled from "styled-components";

import {
    DigitLoading,
    DigitText,
    DigitLayout,
    useDigitTranslations
} from "@cthit/react-digit-components";

import { getApprovals } from "api/approval/get.approval.api";

import translations from "./MeApprovals.translations.screen";
import Approval from "./approval";

const Grid = styled.div`
    flex: 1;

    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(288px, 400px));
    column-gap: 16px;
    row-gap: 16px;

    justify-content: center;

    margin-bottom: calc(56px + 16px);
`;

const MeApprovals = () => {
    const [approvals, setApprovals] = useState(null);
    const [text, activeLanguage] = useDigitTranslations(translations);

    const descriptionKey = activeLanguage + "Description";

    useEffect(() => {
        getApprovals().then(response => {
            const approvals = {};
            response.data.forEach(approval => {
                approvals[approval.clientUid] = approval;
            });
            setApprovals(approvals);
        });
    }, [activeLanguage]);

    if (approvals == null) {
        return <DigitLoading loading alignSelf={"center"} margin={"auto"} />;
    }

    if (Object.keys(approvals).length === 0) {
        return (
            <DigitLayout.Center>
                <DigitText.Text text={text.YouHaveNotApprovedAnyClient} />
            </DigitLayout.Center>
        );
    }

    return (
        <Grid>
            {Object.values(approvals).map(approval => (
                <Approval
                    key={approval.clientUid}
                    name={approval.name}
                    clientUid={approval.clientUid}
                    description={approval[descriptionKey]}
                    onApprovalDeleted={() => {
                        const newApprovals = { ...approvals };
                        delete newApprovals[approval.clientUid];
                        setApprovals(newApprovals);
                    }}
                />
            ))}
        </Grid>
    );
};

export default MeApprovals;

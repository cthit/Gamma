import React from "react";
import translations from "./SetPostNames.view.translations.json";
import {
    DigitButton,
    DigitDesign,
    DigitEditData,
    DigitLayout,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";
import NewMembershipArray from "./sub-views/new-membership-array";

import _ from "lodash";

import * as yup from "yup";
import {
    ACCEPTANCE_YEAR,
    CID,
    FIRST_NAME,
    ID,
    LAST_NAME,
    NICK
} from "../../../../api/users/props.users.api";
import { useHistory } from "react-router-dom";
import Save from "@material-ui/icons/Save";

function getInitialValues(selectedMemberIds, currentMembers, users, groupId) {
    const necessaryMembersData = selectedMemberIds.map(selectedMember => {
        const user = _.find(users, { id: selectedMember });

        const necessaryMemberData = {};
        necessaryMemberData[FIRST_NAME] = user[FIRST_NAME];
        necessaryMemberData[LAST_NAME] = user[LAST_NAME];
        necessaryMemberData[NICK] = user[NICK];
        necessaryMemberData[CID] = user[CID];
        necessaryMemberData[ACCEPTANCE_YEAR] = user[ACCEPTANCE_YEAR];
        necessaryMemberData[ID] = user[ID];

        const previousMemberData = _.find(currentMembers, { id: user.id });

        if (previousMemberData != null) {
            necessaryMemberData.postId = previousMemberData.post.id;
            necessaryMemberData.unofficialPostName =
                previousMemberData.unofficialPostName;
        }

        return necessaryMemberData;
    });

    return {
        members: necessaryMembersData
    };
}

const SetPostNames = ({
    selectedMemberIds,
    groupId,
    posts,
    currentMembers,
    users,
    onNewMembers
}) => {
    const [text] = useDigitTranslations(translations);
    const history = useHistory();

    return (
        <>
            <DigitDesign.Card margin={{ bottom: "16px" }}>
                <DigitDesign.CardBody>
                    <DigitLayout.Row
                        justifyContent={"space-between"}
                        alignItems={"center"}
                    >
                        <DigitText.Heading5 text={text.SetPostNames} />
                        <DigitLayout.Row>
                            <DigitButton
                                text={text.Back}
                                onClick={() => history.goBack()}
                            />
                            <DigitButton
                                text={text.Next}
                                startIcon={<Save />}
                                raised
                                primary
                                submit
                                form={"set-post-names"}
                            />
                        </DigitLayout.Row>
                    </DigitLayout.Row>
                </DigitDesign.CardBody>
            </DigitDesign.Card>
            <DigitDesign.Card>
                <DigitDesign.CardBody>
                    <DigitEditData
                        formName={"set-post-names"}
                        size={{ minWidth: "300px" }}
                        onSubmit={onNewMembers}
                        keysOrder={["members"]}
                        initialValues={getInitialValues(
                            selectedMemberIds,
                            currentMembers,
                            users,
                            groupId
                        )}
                        validationSchema={yup.object().shape({
                            members: yup
                                .array(
                                    yup
                                        .object()
                                        .shape({
                                            postId: yup.string().required(),
                                            unofficialPostName: yup
                                                .string()
                                                .required()
                                        })
                                        .required()
                                )
                                .required()
                        })}
                        keysComponentData={{
                            members: {
                                component: NewMembershipArray,
                                componentProps: {
                                    posts,
                                    currentMembers,
                                    groupId
                                },
                                array: true
                            }
                        }}
                    />
                </DigitDesign.CardBody>
            </DigitDesign.Card>
        </>
    );
};

export default SetPostNames;

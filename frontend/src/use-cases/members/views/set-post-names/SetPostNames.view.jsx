import Save from "@material-ui/icons/Save";
import _ from "lodash";
import React from "react";
import { useHistory } from "react-router-dom";
import * as yup from "yup";

import {
    DigitButton,
    DigitDesign,
    DigitForm,
    DigitLayout,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";

import {
    USER_ACCEPTANCE_YEAR,
    USER_CID,
    USER_FIRST_NAME,
    USER_ID,
    USER_LAST_NAME,
    USER_NICK
} from "api/users/props.users.api";

import translations from "./SetPostNames.view.translations.json";
import NewMembershipArray from "./sub-views/new-membership-array";

function getInitialValues(selectedMemberIds, currentMembers, users) {
    const currentMemberUsers = currentMembers.map(member => member.user);

    const necessaryMembersData = selectedMemberIds.map(selectedMember => {
        const user = _.find(users, { id: selectedMember });

        const necessaryMemberData = {};
        necessaryMemberData[USER_FIRST_NAME] = user[USER_FIRST_NAME];
        necessaryMemberData[USER_LAST_NAME] = user[USER_LAST_NAME];
        necessaryMemberData[USER_NICK] = user[USER_NICK];
        necessaryMemberData[USER_CID] = user[USER_CID];
        necessaryMemberData[USER_ACCEPTANCE_YEAR] = user[USER_ACCEPTANCE_YEAR];
        necessaryMemberData[USER_ID] = user[USER_ID];

        const previousMemberData = _.find(
            currentMembers,
            member => member.user.id === user.id
        );

        var postId = "";
        var unofficialPostName = "";

        if (previousMemberData != null) {
            postId = previousMemberData.post.id;
            unofficialPostName = previousMemberData.unofficialPostName;
        }

        necessaryMemberData.postId = postId;
        necessaryMemberData.unofficialPostName = unofficialPostName;

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
        <DigitForm
            onSubmit={onNewMembers}
            initialValues={getInitialValues(
                selectedMemberIds,
                currentMembers,
                users,
                groupId
            )}
            validationSchema={yup.object().shape({
                members: yup.array().of(
                    yup.object().shape({
                        postId: yup
                            .string()
                            .required(text.Post + text.IsRequired),
                        unofficialPostName: yup.string()
                    })
                )
            })}
            render={({ isValid }) => (
                <>
                    <DigitDesign.Card
                        size={{ minHeight: "min-content" }}
                        margin={{ bottom: "8px" }}
                    >
                        <DigitDesign.CardBody>
                            <DigitLayout.Row
                                justifyContent={"space-between"}
                                alignItems={"center"}
                                flexWrap={"wrap"}
                            >
                                <DigitText.Heading5 text={text.SetPostNames} />
                                <DigitLayout.Row>
                                    <DigitButton
                                        outlined
                                        text={text.Back}
                                        onClick={() => history.goBack()}
                                    />
                                    <DigitButton
                                        text={text.Next}
                                        startIcon={<Save />}
                                        raised
                                        primary
                                        submit
                                        disabled={!isValid}
                                    />
                                </DigitLayout.Row>
                            </DigitLayout.Row>
                        </DigitDesign.CardBody>
                    </DigitDesign.Card>
                    <DigitDesign.Card>
                        <DigitDesign.CardBody>
                            {selectedMemberIds.length === 0 && (
                                <DigitText.Title
                                    alignCenter
                                    text={text.NoSelectedMembers}
                                />
                            )}
                            <NewMembershipArray posts={posts} />
                        </DigitDesign.CardBody>
                    </DigitDesign.Card>
                </>
            )}
        />
    );
};

export default SetPostNames;

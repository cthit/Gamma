import React from "react";

import Save from "@material-ui/icons/Save";

import {
    DigitFAB,
    DigitLayout,
    DigitDesign,
    useDigitTranslations
} from "@cthit/react-digit-components";
import NewMember from "./elements/new-member";

import * as _ from "lodash";
import translations from "./ReviewChanges.view.translations";
import {
    FIRST_NAME,
    LAST_NAME,
    NICK
} from "../../../../api/users/props.users.api";
import { editUserInGroup } from "../../../../api/groups/put.groups.api";
import { removeUserFromGroup } from "../../../../api/groups/delete.groups.api";
import { addUserToGroup } from "../../../../api/groups/post.groups.api";

function getAdditions(previousMembers, newMembers) {
    return newMembers.filter(
        newMember => _.findIndex(previousMembers, ["id", newMember.id]) === -1
    );
}

function getDeletions(previousMembers, newMembers) {
    return previousMembers.filter(
        previousMember =>
            _.findIndex(newMembers, ["id", previousMember.id]) === -1
    );
}

function getEdits(previousMembers, newMembers) {
    return _.intersectionWith(
        previousMembers,
        newMembers,
        (previousMember, newMember) => previousMember.id === newMember.id
    );
}

const ReviewChanges = ({
    groupName,
    previousMembers,
    groupId,
    posts,
    onFinished,
    newMembersData
}) => {
    const [text, activeLanguage] = useDigitTranslations(translations);

    return (
        <DigitLayout.Center>
            <>
                <DigitDesign.Card>
                    <DigitDesign.CardTitle
                        text={text.NewMembersForGroup + " " + groupName}
                    />
                    <DigitDesign.CardBody minWidth={"280px"}>
                        {newMembersData.map(member => (
                            <NewMember
                                key={member.id}
                                firstName={member[FIRST_NAME]}
                                lastName={member[LAST_NAME]}
                                nick={member[NICK]}
                                unofficialPostName={member.unofficialPostName}
                                activeLanguage={activeLanguage}
                                post={_.find(posts, {
                                    id: member.postId
                                })}
                            />
                        ))}
                    </DigitDesign.CardBody>
                </DigitDesign.Card>
                <DigitLayout.DownRightPosition>
                    <DigitFAB
                        icon={Save}
                        primary
                        onClick={() => {
                            const additions = getAdditions(
                                previousMembers,
                                newMembersData
                            ).map(member =>
                                addUserToGroup(groupId, {
                                    userId: member.id,
                                    post: member.postId,
                                    unofficialName: member.unofficialPostName
                                })
                            );

                            const deletions = getDeletions(
                                previousMembers,
                                newMembersData
                            ).map(previousMember =>
                                removeUserFromGroup(groupId, previousMember.id)
                            );

                            const edits = getEdits(
                                previousMembers,
                                newMembersData
                            ).map(member =>
                                editUserInGroup(groupId, member.id, {
                                    userId: member.id,
                                    post: member.post.id,
                                    unofficialName: member.unofficialPostName
                                })
                            );

                            Promise.all([...additions, ...deletions, ...edits])
                                .then(() => {
                                    onFinished();
                                })
                                .catch(e => {
                                    console.log(e);
                                    // onFinished();
                                });
                        }}
                    />
                </DigitLayout.DownRightPosition>
            </>
        </DigitLayout.Center>
    );
};

export default ReviewChanges;

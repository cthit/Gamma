import React from "react";

import Save from "@material-ui/icons/Save";

import {
    DigitFAB,
    DigitLayout,
    DigitDesign,
    DigitTranslations
} from "@cthit/react-digit-components";
import NewMember from "./elements/new-member";

import * as _ from "lodash";
import translations from "./ReviewChanges.view.translations";
import {
    FIRST_NAME,
    LAST_NAME,
    NICK
} from "../../../../api/users/props.users.api";

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
    redirectTo,
    addUserToGroup,
    removeUserFromGroup,
    editUserInGroup,
    posts,
    onFinished
}) => {
    var savedPostNames = sessionStorage.getItem(groupId + ".postNames");
    if (savedPostNames == null) {
        redirectTo("/groups/" + groupId + "/members");
        return null;
    }

    const members = JSON.parse(savedPostNames).members;

    return (
        <DigitTranslations
            translations={translations}
            render={(text, activeLanguage) => (
                <DigitLayout.Center>
                    <>
                        <DigitDesign.Card>
                            <DigitDesign.CardTitle
                                text={text.NewMembersForGroup + " " + groupName}
                            />
                            <DigitDesign.CardBody minWidth={"280px"}>
                                {members.map(member => (
                                    <NewMember
                                        key={member.id}
                                        firstName={member[FIRST_NAME]}
                                        lastName={member[LAST_NAME]}
                                        nick={member[NICK]}
                                        unofficialPostName={
                                            member.unofficialPostName
                                        }
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
                                        members
                                    ).map(member =>
                                        addUserToGroup(groupId, {
                                            userId: member.id,
                                            post: member.postId,
                                            unofficialName:
                                                member.unofficialPostName
                                        })
                                    );

                                    const deletions = getDeletions(
                                        previousMembers,
                                        members
                                    ).map(previousMember =>
                                        removeUserFromGroup(
                                            groupId,
                                            previousMember.id
                                        )
                                    );

                                    const edits = getEdits(
                                        previousMembers,
                                        members
                                    ).map(member =>
                                        editUserInGroup(groupId, member.id, {
                                            userId: member.id,
                                            post: member.post.id,
                                            unofficialName:
                                                member.unofficialPostName
                                        })
                                    );

                                    Promise.all([
                                        ...additions,
                                        ...deletions,
                                        ...edits
                                    ])
                                        .then(() => {
                                            sessionStorage.clear();
                                            onFinished();
                                        })
                                        .catch(() => {
                                            sessionStorage.clear();
                                            onFinished();
                                        });
                                }}
                            />
                        </DigitLayout.DownRightPosition>
                    </>
                </DigitLayout.Center>
            )}
        />
    );
};

export default ReviewChanges;

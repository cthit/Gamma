import React from "react";

import {
    DigitTable,
    useDigitTranslations
} from "@cthit/react-digit-components";

import { GROUP_ID, GROUP_NAME } from "api/groups/props.groups.api";

function modifyData(groups, activeLanguage) {
    const newGroups = {};

    groups.forEach(({ group, post }) => {
        const groupId = group[GROUP_ID];
        if (groupId in newGroups) {
            newGroups[groupId].posts.push(post[activeLanguage + "Name"]);
        } else {
            newGroups[groupId] = {
                ...group,
                posts: [post[activeLanguage + "Name"]]
            };
        }
    });

    return Object.keys(newGroups).map(groupId => {
        const group = newGroups[groupId];
        group["__link"] = "/groups/" + groupId;
        group["posts"] = group.posts.join(", ");

        return group;
    });
}

const UserGroupsTable = ({ title, groups, margin = {} }) => {
    const [text, activeLanguage] = useDigitTranslations();

    if (groups == null) {
        return null;
    }

    return (
        <DigitTable
            size={{ width: "100%" }}
            margin={margin}
            titleText={title ? title : text.Groups}
            searchText={text.SearchForGroups}
            idProp="id"
            startOrderBy={GROUP_NAME}
            columnsOrder={["prettyName", "posts"]}
            headerTexts={{
                prettyName: text.Name,
                posts: text.Post
            }}
            data={modifyData(groups, activeLanguage)}
            emptyTableText={text.NoGroups}
        />
    );
};

export default UserGroupsTable;

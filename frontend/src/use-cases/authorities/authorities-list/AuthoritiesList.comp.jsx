import React from "react";
import {
    DigitList,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";
import translations from "./AuthoritiesList.comp.translations.json";
import DeleteIcon from "@material-ui/icons/Delete";

const AuthoritiesList = ({
    users = [],
    superGroups = [],
    posts = [],
    itemOnClick
}) => {
    const [text, activeLanguage] = useDigitTranslations(translations);

    if (users.length + superGroups.length + posts.length === 0) {
        return <DigitText.Text text={text.NoAuthorities} />;
    } else {
        return (
            <>
                <div style={{ overflowY: "auto" }}>
                    <DigitText.Text bold text={text.UsersAuthorities} />
                    <DigitList
                        items={users.map(user => ({
                            text:
                                user.firstName +
                                ' "' +
                                user.nick +
                                '" ' +
                                user.lastName,
                            actionIcon: itemOnClick == null ? null : DeleteIcon,
                            actionOnClick:
                                itemOnClick == null
                                    ? null
                                    : () => itemOnClick(user, "user")
                        }))}
                        onClick={null}
                        dense
                    />
                    <DigitText.Text bold text={text.SuperGroupAuthorities} />
                    <DigitList
                        items={superGroups.map(superGroup => ({
                            text: superGroup.prettyName,
                            actionIcon: itemOnClick == null ? null : DeleteIcon,
                            actionOnClick:
                                itemOnClick == null
                                    ? null
                                    : () =>
                                          itemOnClick(superGroup, "superGroup")
                        }))}
                        onClick={null}
                        dense
                    />
                    <DigitText.Text bold text={text.PostAuthorities} />
                    <DigitList
                        items={posts.map(({ post, superGroup }) => ({
                            text:
                                superGroup.prettyName +
                                " - " +
                                post[activeLanguage],
                            actionIcon: itemOnClick == null ? null : DeleteIcon,
                            actionOnClick:
                                itemOnClick == null
                                    ? null
                                    : () =>
                                          itemOnClick(
                                              { post, superGroup },
                                              "post"
                                          )
                        }))}
                        onClick={null}
                        dense
                    />
                </div>
            </>
        );
    }
};

export default AuthoritiesList;

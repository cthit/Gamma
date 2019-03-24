import React from "react";

import {
    DigitLayout,
    DigitTextField,
    DigitTranslations,
    DigitText,
    DigitSelect
} from "@cthit/react-digit-components";

import translations from "./CreatePost.view.translations";
import {
    FIRST_NAME,
    LAST_NAME,
    NICK
} from "../../../../../../../../api/users/props.users.api";

class CreatePost extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            postId:
                props.previousMemberData != null
                    ? props.previousMemberData.post.id
                    : "",
            unofficialPostName:
                props.previousMemberData != null
                    ? props.previousMemberData.unofficialPostName
                    : ""
        };
    }

    _getDifferentPostNames = (posts, activeLanguage) => {
        const output = {};
        posts.forEach(post => {
            output[post.id] = post[activeLanguage];
        });

        return output;
    };

    render() {
        const { postId, unofficialPostName } = this.state;
        const { posts, member } = this.props;

        return (
            <DigitTranslations
                translations={translations}
                render={(text, activeLanguage) => (
                    <DigitLayout.Row centerVertical>
                        <DigitText.Text
                            text={
                                member[FIRST_NAME] +
                                ' "' +
                                member[NICK] +
                                '" ' +
                                member[LAST_NAME]
                            }
                        />
                        <DigitLayout.Size absWidth="200px">
                            <DigitSelect
                                value={postId}
                                onChange={e => {
                                    this.setState({
                                        postId: e.target.value
                                    });
                                }}
                                valueToTextMap={this._getDifferentPostNames(
                                    posts,
                                    activeLanguage
                                )}
                                upperLabel={text.Post}
                            />
                        </DigitLayout.Size>
                        <DigitLayout.Size absWidth="200px">
                            <DigitTextField
                                upperLabel={text.UnofficialPostName}
                                value={unofficialPostName}
                                onChange={e => {
                                    this.setState({
                                        unofficialPostName: e.target.value
                                    });
                                }}
                            />
                        </DigitLayout.Size>
                    </DigitLayout.Row>
                )}
            />
        );
    }
}

export default CreatePost;

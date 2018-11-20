import {
    DigitButton,
    DigitTextField,
    DigitSelect
} from "@cthit/react-digit-components";
import React from "react";
import { Fill, Size, Spacing, VerticalFill } from "../../../common-ui/layout";
import EditableWebsite from "./sub-views/editable-website";

class EditWebsites extends React.Component {
    state = {
        newWebsiteTypeSelected: "",
        newWebsiteUrl: ""
    };

    /**
   * {
            type: yup.string().required(),
            url: yup.string().required()
          }
   */

    /**
     *
     */

    _getValueToTextMap(availableWebsites) {
        const output = {};

        availableWebsites.forEach(website => {
            output[website.name] = website.prettyName;
        });

        return output;
    }

    render() {
        const { push, replace, remove, form, availableWebsites } = this.props;

        return (
            <Fill>
                <VerticalFill>
                    <Size minWidth="150px">
                        <DigitSelect
                            valueToTextMap={this._getValueToTextMap(
                                availableWebsites
                            )}
                            upperLabel="Typ"
                            value={this.state.newWebsiteTypeSelected}
                            onChange={e => {
                                this.setState({
                                    newWebsiteTypeSelected: e.target.value
                                });
                            }}
                        />
                    </Size>

                    <Spacing />

                    <DigitTextField
                        upperLabel="URL"
                        value={this.state.newWebsiteUrl}
                        onChange={e => {
                            this.setState({
                                newWebsiteUrl: e.target.value
                            });
                        }}
                    />

                    <Spacing />

                    <DigitButton
                        outline
                        text="Add website"
                        onClick={() => {
                            push({
                                website: this.state.newWebsiteTypeSelected,
                                url: this.state.newWebsiteUrl
                            });
                            this.setState({
                                newWebsiteUrl: ""
                            });
                        }}
                    />
                </VerticalFill>
                <Fill>
                    {form.values.websites.map((website, index) => (
                        <EditableWebsite
                            key={website.url}
                            website={website.website}
                            prettyWebsite={website.website}
                            url={website.url}
                            onChange={newWebsite => {
                                replace(index, newWebsite);
                            }}
                            onDelete={() => {
                                remove(index);
                            }}
                        />
                    ))}
                </Fill>
            </Fill>
        );
    }
}

export default EditWebsites;

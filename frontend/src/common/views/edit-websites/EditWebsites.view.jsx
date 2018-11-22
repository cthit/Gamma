import {
    DigitButton,
    DigitTextField,
    DigitSelect,
    DigitLayout
} from "@cthit/react-digit-components";
import React from "react";
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
            <DigitLayout.Fill>
                <DigitLayout.Column>
                    <DigitLayout.Size minWidth="150px">
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
                    </DigitLayout.Size>

                    <DigitLayout.Spacing />

                    <DigitTextField
                        upperLabel="URL"
                        value={this.state.newWebsiteUrl}
                        onChange={e => {
                            this.setState({
                                newWebsiteUrl: e.target.value
                            });
                        }}
                    />

                    <DigitLayout.Spacing />

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
                </DigitLayout.Column>
                <DigitLayout.Fill>
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
                </DigitLayout.Fill>
            </DigitLayout.Fill>
        );
    }
}

export default EditWebsites;

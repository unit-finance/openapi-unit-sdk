# frozen_string_literal: true

require 'openapi_client'
require_relative '../spec_helper'
require 'json'
require 'date'

RSpec.describe 'ATMLocation' do
  before do
    # run before each test
    configure_tests
  end

  describe 'get ATM locations' do
    let(:api_instance) { OpenapiClient::GetATMLocationsListApi.new(OpenapiClient::ApiClient.new(configuration)) }


    it 'should get ATM locations by coordinates' do
      opts = {
        query_params: {
          'filter[postalCode]': "02139",
          'filter[searchRadius]': 30
        }      }
      response = api_instance.execute(opts)
      expect(response.data.type).to eq('atmLocation')
    end

    it 'should get ATM locations by coordinates' do
      search_radius = 30

      coordinates = OpenapiClient::Coordinates.new({ latitude: 40.730610, longitude: -71.935242 })

      coordinates_json = coordinates.to_hash.to_json

      opts = {
        query_params: {
          'filter[coordinates]': coordinates_json,
          'filter[searchRadius]': search_radius
        }
      }

      response = api_instance.execute(opts)
      response[:data].each do |atm_location|
        expect(atm_location[:type]).to eq('atmLocation')
      end
    end
  end
end